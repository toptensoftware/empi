FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://wpa_supplicant.conf-sane \
    file://wpa-supplicant-wlan0 \
"

inherit update-rc.d

INITSCRIPT_NAME = "wpa-supplicant-wlan0"
INITSCRIPT_PARAMS = "defaults 25"

do_install:append() {
    # Install wpa_supplicant config
    install -d ${D}${sysconfdir}/wpa_supplicant
    install -m 600 ${WORKDIR}/wpa_supplicant.conf-sane ${D}${sysconfdir}/wpa_supplicant/wpa_supplicant-wlan0.conf
    
    # Install init script
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/wpa-supplicant-wlan0 ${D}${sysconfdir}/init.d/
}

FILES:${PN} += " \
    ${sysconfdir}/wpa_supplicant/wpa_supplicant-wlan0.conf \
    ${sysconfdir}/init.d/wpa-supplicant-wlan0 \
"
