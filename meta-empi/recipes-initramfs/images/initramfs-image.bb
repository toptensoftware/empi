DESCRIPTION = "Minimal initramfs image for early boot"

# Include busybox (needed for /bin/sh during package installation, but not used at runtime)
# and our custom zmach app with C init
PACKAGE_INSTALL = "busybox zmach"

# Add required libraries for OpenGL/graphics
PACKAGE_INSTALL += "zlib expat libgcc libstdc++ libdrm mesa-megadriver"

# Add MMC modules needed to mount rootfs
PACKAGE_INSTALL += "kernel-module-mmc-core"
PACKAGE_INSTALL += "kernel-module-mmc-block"
PACKAGE_INSTALL += "kernel-module-bcm2835-sdhost"
PACKAGE_INSTALL += "kernel-module-ext4"

# This is an initramfs image
IMAGE_FSTYPES = "cpio.lz4"

# Minimal size
IMAGE_ROOTFS_SIZE = "8192"
IMAGE_ROOTFS_EXTRA_SPACE = "0"

# No package management
IMAGE_FEATURES = ""

# Inherit the core image class
inherit core-image

# Copy init script to root of initramfs
ROOTFS_POSTPROCESS_COMMAND += "install_init_script; remove_kernel_image; remove_busybox_runtime; decompress_modules;"

install_init_script() {
    # Create mount points
    install -d ${IMAGE_ROOTFS}/proc
    install -d ${IMAGE_ROOTFS}/sys
    install -d ${IMAGE_ROOTFS}/dev
    install -d ${IMAGE_ROOTFS}/mnt/root
    
    # Copy init binary
    install -m 0755 ${IMAGE_ROOTFS}/init.d/init ${IMAGE_ROOTFS}/init
    
    # Remove
    rm -rf ${IMAGE_ROOTFS}/init.d
}

remove_kernel_image() {
    # Remove kernel image from initramfs - we don't need it
    rm -rf ${IMAGE_ROOTFS}/boot
}

remove_busybox_runtime() {
    # Remove busybox binaries after package installation
    # Keep only what's absolutely necessary (none for our C init!)
    rm -rf ${IMAGE_ROOTFS}/bin/busybox ${IMAGE_ROOTFS}/bin/busybox.* 
    rm -rf ${IMAGE_ROOTFS}/bin ${IMAGE_ROOTFS}/sbin ${IMAGE_ROOTFS}/usr/bin ${IMAGE_ROOTFS}/usr/sbin
    # Recreate empty directories in case something references them
    mkdir -p ${IMAGE_ROOTFS}/bin ${IMAGE_ROOTFS}/sbin
}

decompress_modules() {
    # Decompress all .ko.xz modules in place
    bbnote "Decompressing kernel modules for initramfs..."
    find ${IMAGE_ROOTFS}/lib/modules -name "*.ko.xz" | while read module; do
        unxz "${module}"
    done
}