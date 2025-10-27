# Yocto Build for Raspberry Pi Zero W

Minimal Yocto setup for Raspberry Pi Zero W with WiFi support.

## Requirements

- Ubuntu 20.04+ or Debian-based Linux
- 50-100GB free disk space
- 8GB+ RAM (16GB recommended)
- Internet connection

## Quick Start
```bash
# Clone this repository
git clone <your-repo-url> yocto-rpi-project
cd yocto-rpi-project

# Initialize submodules
./setup.sh

# Enter build environment
source poky/oe-init-build-env build

# Build the image
bitbake core-image-minimal
```

## Configuration

Configuration files are version controlled in `build/conf/`:
- `local.conf` - Machine and build settings
- `bblayers.conf` - Yocto layers configuration

Edit these files to customize your build.

## Subsequent Builds
```bash
cd yocto-rpi-project
source poky/oe-init-build-env build
bitbake core-image-minimal
```

## Flash to SD Card
```bash
cd build/tmp/deploy/images/raspberrypi0-wifi/

# Find your SD card device
lsblk

# Flash the image (choose one)
# Method 1: bmaptool (recommended)
sudo bmaptool copy core-image-minimal-raspberrypi0-wifi.rootfs.wic.bz2 /dev/sdX

# Method 2: dd
bunzip2 -c core-image-minimal-raspberrypi0-wifi.rootfs.wic.bz2 | \
  sudo dd of=/dev/sdX bs=4M status=progress && sync
```

Replace `/dev/sdX` with your actual SD card device.

## Updating Submodules
```bash
git submodule update --remote --merge
git add poky meta-raspberrypi meta-openembedded
git commit -m "Update submodules"
```

## Build Output

Images will be in: `build/tmp/deploy/images/raspberrypi0-wifi/`

