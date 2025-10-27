#!/bin/bash

set -e

echo "Initializing submodules..."
git submodule update --init --recursive

echo ""
echo "=========================================="
echo "Setup complete!"
echo "=========================================="
echo "To start building:"
echo "  source poky/oe-init-build-env build"
echo "  bitbake core-image-minimal"
echo ""
echo "Configuration files are in build/conf/"
echo "=========================================="

