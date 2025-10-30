do_deploy:append() {

    CONFIG=${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    
    # Use firmware KMS for faster DRM init (like buildroot)
    sed -i 's/dtoverlay=vc4-kms-v3d/dtoverlay=vc4-fkms-v3d/' $CONFIG
    
    # Add initramfs loading to config.txt
    echo "initramfs initramfs.cpio.lz4 followkernel" >> $CONFIG

}


