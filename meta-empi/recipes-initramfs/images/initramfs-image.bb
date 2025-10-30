DESCRIPTION = "Minimal initramfs image for early boot"

# Include busybox and our custom zmach app
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
ROOTFS_POSTPROCESS_COMMAND += "install_init_script;"

install_init_script() {
    
    # Create mount points
    install -d ${IMAGE_ROOTFS}/proc
    install -d ${IMAGE_ROOTFS}/sys
    install -d ${IMAGE_ROOTFS}/dev
    install -d ${IMAGE_ROOTFS}/mnt/root

    # Copy install script
    install -m 0755 ${IMAGE_ROOTFS}/init.d/init ${IMAGE_ROOTFS}/init

    # Remove
    rm -rf ${IMAGE_ROOTFS}/init.d
}