#!/bin/bash


# Install PyTorch 2.1.0, Torchvision 0.16.0, and the corresponding Torchaudio with CUDA 12.1 support
pip install torch==2.1.0 torchvision==0.16.0 torchaudio==2.1.0 --index-url https://download.pytorch.org/whl/cu121
pip install torchsummary==1.5.1
# Verify installations
python -c "import torch; import torchvision; import torchaudio; print(f'PyTorch version: {torch.__version__}'); print(f'Torchvision version: {torchvision.__version__}'); print(f'Torchaudio version: {torchaudio.__version__}'); print(f'CUDA available: {torch.cuda.is_available()}'); print(f'CUDA version: {torch.version.cuda}')"

echo "Installation complete. Please check for any errors."
