FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}-1.0:"

SRC_URI += "file://usb-keyboard"

do_install:append() {
    install -m 0755 ${WORKDIR}/usb-keyboard ${D}${sysconfdir}/init.d/usb-keyboard
    update-rc.d -r ${D} usb-keyboard start 07 S .
}