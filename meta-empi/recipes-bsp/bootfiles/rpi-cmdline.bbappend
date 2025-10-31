FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = "file://cmdline.txt"

do_compile() {
    # File is already in WORKDIR from SRC_URI, nothing to do
    :
}