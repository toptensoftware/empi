DESCRIPTION = "Custom OpenGL app for initramfs"
LICENSE = "CLOSED"

SRC_URI = "file://zmach \
           file://init"

S = "${WORKDIR}"

# Declare runtime dependencies for the libraries zmach needs
RDEPENDS:${PN} = "mesa libdrm"

# Skip QA checks for prebuilt binary - it can't auto-detect dependencies
INSANE_SKIP:${PN} = "ldflags already-stripped 32bit-time file-rdeps"

do_install() {
    install -d ${D}/home/root
    install -m 0755 ${WORKDIR}/zmach ${D}/home/root/zmach
    
    install -d ${D}/init.d
    install -m 0755 ${WORKDIR}/init ${D}/init.d/init
}

FILES:${PN} = "/home/root/zmach /init.d/init"