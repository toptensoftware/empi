FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://config.txt"

do_deploy:append() {

    install -m 0644 ${WORKDIR}/config.txt ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt

}


