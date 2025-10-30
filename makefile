# Helper to run bitbake commands
define bitbake
	bash -c "source poky/oe-init-build-env build > /dev/null && bitbake $(1)"
endef

distro:
	$(call bitbake,core-image-minimal)

flash:
	./sdflash

clean:
	rm -rf ./build/tmp

sdk:
	$(call bitbake,core-image-minimal -c populate_sdk)
	scp \
		/home/brad/Projects/empi/build/tmp/deploy/sdk/empi-glibc-x86_64-core-image-minimal-arm1176jzfshf-vfp-raspberrypi0-wifi-toolchain-5.0.13.sh \
		brad@cool:/home/brad/transfer/