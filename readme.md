# Raspberry Pi Zero Yocto Project Guide

## Project Overview

Custom Yocto-based Linux system for Raspberry Pi Zero W running a minimal BusyBox environment and hosting a custom TRS-80 Model 1 emulator (zmach).

A key goal of this system is to have the app appear on screen as soon as possible so zmach is launched from initramfs after which the full root file 
system is mounted switched to. Services like, USB, Keyboard, Wifi, Dropbear are started after switching root and via the normal busybox inittab/init.d.

Since zmach uses OpenGL for rendering (via DRM) the initramfs needs to include everything need to get that stack up and running (ie: DRM, KMS, MESA,
Gallium/OpenGL etc...)

## Porting from BuildRoot

This project is a port of an existing buildroot based system.  While this new Yocto based system currently runs the target applications it's somewhat
bloated and slower to boot than the original buildroot system.

Primary goal for this project right now is trimming the system for size and boot speed:

* Adjusting built-in vs loadable kernel modules
* Removing any optional functionality from kernel that slows boot time
* Reducing the size of the initramfs (eg: by converting init script to .C and removing busybox from the initramfs)
* Reviewing and comparing dmesg logs to improve boot time
* Triming anything from the boot partition, config.txt and cmdline.txt that slows launching of the kernel.

As a baseline, the buildroot system currently boots to kernel in about 6 seconds and zmach is launched at about the 1 second mark in the dmesg log.

By comparison, for the yocto system time to kernel load is similar, but zmach doesn't launch until the the 3.3 second mark.


## Initial Setup

Install prerequisites:

```
sudo apt-get install gawk wget git diffstat unzip texinfo gcc build-essential \
chrpath socat cpio python3 python3-pip python3-pexpect xz-utils debianutils \
iputils-ping python3-git python3-jinja2 libegl1 libsdl1.2-dev pylint \
python3-subunit mesa-common-dev zstd liblz4-tool file
```

Install required locale:

```
sudo locale-gen en_US.UTF-8
sudo update-locale
```

Init repository:

```
git submodule update --init --recursive
```

## Make Commands

Rather than use bitbake commands which tend to be verbose, there's an included
makefile with various targets including:

`make distro` - build the main image => bitbake core-image-minimal
`make flash` - flash to SD card (configure via `sdflash` script)
`make sdk` - builds the SDK and copies to \\cool\brad\transfer

Also, there's a script `env` in the project root directory that setups the
yocto/bitbake build environment:

```bash
brad@BRADDEV:~/Projects/empi$ . e
```

## Hardware
- **Target Device**: Raspberry Pi Zero W Rev 1.1
- **GPU**: VideoCore IV (VC4)
- **Boot**: SD card (/dev/mmcblk0)

## System Architecture

### Boot Process

1. **Kernel boots** (Linux 6.6.63)
2. **Initramfs loads** into memory
3. **Init script runs** from initramfs:
   - Mounts essential filesystems (/dev, /proc, /sys)
   - Starts zmach application with DRM/KMS graphics
   - Mounts root filesystem from SD card (/dev/mmcblk0p2)
   - Performs switch_root to full filesystem
4. **Full system starts**

### Yocto Project Structure
- **Project Location**: `/home/brad/Projects/empi`
- **Build Directory**: `build/`
- **Machine**: `raspberrypi0-wifi`
- **Distro**: Poky-based with custom configuration

### Key Images

1. **Main Image**: `core-image-minimal`
   - Full root filesystem on SD card partition 2
   - Contains complete system with all packages
   
2. **Initramfs Image**: `initramfs-image`
   - Minimal early-boot environment
   - Loaded separately, referenced from config.txt
   - Contains only essential packages for early boot

## Application (zmach)

### Runtime Requirements
- **Graphics API**: EGL/OpenGL ES (via Mesa)
- **Display Mode**: DRM/KMS (Direct Rendering Manager / Kernel Mode Setting)
- **Dependencies**: libdrm, Mesa drivers, standard C/C++ libraries

