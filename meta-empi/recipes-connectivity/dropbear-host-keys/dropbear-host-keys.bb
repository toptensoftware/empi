SUMMARY = "Persistent SSH host keys"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://dropbear_rsa_host_key \
    file://dropbear_ecdsa_host_key \
    file://dropbear_ed25519_host_key\
"

do_install() {
    install -d ${D}${sysconfdir}/dropbear
    install -m 0600 ${WORKDIR}/dropbear_rsa_host_key ${D}${sysconfdir}/dropbear/dropbear_rsa_host_key
    install -m 0600 ${WORKDIR}/dropbear_ecdsa_host_key ${D}${sysconfdir}/dropbear/dropbear_ecdsa_host_key
    install -m 0600 ${WORKDIR}/dropbear_ed25519_host_key ${D}${sysconfdir}/dropbear/dropbear_ed25519_host_key
}

# Make sure these non-standard file locations are included in image
FILES:${PN} = "${sysconfdir}/dropbear/dropbear_*_host_key"

# Make sure dropbear doesn't regenerate keys
PACKAGE_ARCH = "${MACHINE_ARCH}"
