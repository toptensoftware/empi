# Yocto Build for Raspberry Pi Zero W

## Utils

In home directory of project:

* ./b - build the distro
* ./sdflash - copy to SD card


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
