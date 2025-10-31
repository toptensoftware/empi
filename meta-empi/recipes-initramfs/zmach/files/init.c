#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/mount.h>
#include <sys/stat.h>
#include <sys/syscall.h>
#include <sys/wait.h>
#include <sys/utsname.h>
#include <errno.h>

#define KMSG_PATH "/dev/kmsg"

static void kmsg_write(const char *msg) {
    int fd = open(KMSG_PATH, O_WRONLY);
    if (fd >= 0) {
        write(fd, msg, strlen(msg));
        write(fd, "\n", 1);
        close(fd);
    }
}

static int load_module(const char *path) {
    char fullpath[512];
    struct utsname uts;
    int fd, ret;
    
    uname(&uts);
    snprintf(fullpath, sizeof(fullpath), "/lib/modules/%s/%s.ko", uts.release, path);
    
    fd = open(fullpath, O_RDONLY | O_CLOEXEC);
    if (fd < 0) {
        char msg[256];
        snprintf(msg, sizeof(msg), "ERROR: Failed to open %s: %s", fullpath, strerror(errno));
        kmsg_write(msg);
        return -1;
    }
    
    ret = syscall(SYS_finit_module, fd, "", 0);
    if (ret < 0) {
        char msg[256];
        snprintf(msg, sizeof(msg), "ERROR: Failed to load %s: %s", fullpath, strerror(errno));
        kmsg_write(msg);
    }
    close(fd);
    return ret;
}

static int wait_for_device(const char *devpath, int max_tries) {
    struct stat st;
    int count = 0;
    
    while (count < max_tries) {
        if (stat(devpath, &st) == 0 && S_ISBLK(st.st_mode)) {
            return 0;
        }
        usleep(100000); // 100ms
        count++;
    }
    return -1;
}

int main(void) {
    pid_t pid;
    
    // Mount essential filesystems
    mount("devtmpfs", "/dev", "devtmpfs", 0, NULL);
    mount("proc", "/proc", "proc", 0, NULL);
    mount("sysfs", "/sys", "sysfs", 0, NULL);
    
    // Start zmach in background
    kmsg_write("zmach: starting");
    pid = fork();
    if (pid == 0) {
        // Child process - start zmach
        int fd = open(KMSG_PATH, O_WRONLY);
        dup2(fd, STDOUT_FILENO);
        dup2(fd, STDERR_FILENO);
        close(fd);
        execl("/home/root/zmach", "zmach", NULL);
        _exit(1);
    }
    kmsg_write("zmach: started");
    
    // Load MMC modules in dependency order
    kmsg_write("Loading MMC modules...");
    load_module("kernel/drivers/mmc/core/mmc_core");
    load_module("kernel/drivers/mmc/host/mmc_hsq");
    load_module("kernel/drivers/mmc/host/bcm2835-sdhost");
    load_module("kernel/drivers/mmc/core/mmc_block");
    
    // Load ext4 dependencies
    kmsg_write("Loading ext4 modules...");
    load_module("kernel/lib/crc16");
    load_module("kernel/fs/mbcache");
    load_module("kernel/fs/jbd2/jbd2");
    load_module("kernel/fs/ext4/ext4");
    
    // Wait for block device
    kmsg_write("Waiting for root device /dev/mmcblk0p2...");
    if (wait_for_device("/dev/mmcblk0p2", 50) < 0) {
        kmsg_write("ERROR: Timeout waiting for /dev/mmcblk0p2");
        // Continue anyway, mount will fail and that's obvious
    }
    
    kmsg_write("Found /dev/mmcblk0p2, mounting...");
    
    // Mount the real rootfs
    if (mount("/dev/mmcblk0p2", "/mnt/root", "ext4", 0, NULL) < 0) {
        char msg[256];
        snprintf(msg, sizeof(msg), "ERROR: Failed to mount rootfs: %s", strerror(errno));
        kmsg_write(msg);
        while(1) sleep(1); // Hang on error
    }
    
    mount("none", "/mnt/root/dev", "devtmpfs", 0, NULL);
    
    // Clean up
    umount("/proc");
    umount("/sys");
    
    // Switch to the real rootfs
    if (chdir("/mnt/root") < 0 ||
        mount(".", "/", NULL, MS_MOVE, NULL) < 0 ||
        chroot(".") < 0) {
        char msg[256];
        snprintf(msg, sizeof(msg), "ERROR: Failed to switch root: %s", strerror(errno));
        kmsg_write(msg);
        while(1) sleep(1);
    }
    
    // Exec the real init
    execl("/sbin/init", "init", NULL);
    
    // Should never reach here
    char msg[256];
    snprintf(msg, sizeof(msg), "ERROR: Failed to exec /sbin/init: %s", strerror(errno));
    kmsg_write(msg);
    while(1) sleep(1);
    
    return 1;
}