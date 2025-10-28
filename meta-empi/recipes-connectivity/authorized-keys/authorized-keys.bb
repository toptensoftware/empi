SUMMARY = "Install Dropbear authorized_keys for root user"
DESCRIPTION = "Installs SSH public keys to /etc/dropbear/authorized_keys"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://authorized_keys"

S = "${WORKDIR}"

do_install() {
    install -d ${D}/etc/dropbear
    install -m 0600 ${WORKDIR}/authorized_keys ${D}/etc/dropbear/authorized_keys
    
    # Set proper ownership (will be root:root in the image)
    chown root:root ${D}/etc/dropbear/authorized_keys
}

FILES:${PN} = "/etc/dropbear/authorized_keys"

# Ensure dropbear is installed
RDEPENDS:${PN} = "dropbear"