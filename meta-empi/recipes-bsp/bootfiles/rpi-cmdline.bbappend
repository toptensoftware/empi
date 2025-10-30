# Override to provide empty cmdline.txt
do_compile() {
    echo "" > "${WORKDIR}/cmdline.txt"
}