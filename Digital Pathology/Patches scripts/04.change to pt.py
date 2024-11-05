import torch
import torchvision.models as models

# File paths
pth_file_path = r'C:\Users\carlos01\QuPath\v0.4\wsinfer\local\optima\main\model.pth'
pt_file_path = r'C:\Users\carlos01\QuPath\v0.4\wsinfer\local\optima\main\model.pt'

# Load the full model
full_model = torch.load(pth_file_path, map_location=torch.device('cpu'))

# Create a new instance of the same model architecture
model = models.resnet50()

# Modify the final fully connected layer to match the number of classes in your saved model
num_ftrs = model.fc.in_features
num_classes = 8  # Change this to match the number of classes in your saved model
model.fc = torch.nn.Linear(num_ftrs, num_classes)

# Copy the state dict from the loaded model to the new model
model.load_state_dict(full_model.state_dict())

# Set the model to evaluation mode
model.eval()

# Create an example input tensor
example_input = torch.rand(1, 3, 224, 224)

# Trace the model
traced_script_module = torch.jit.trace(model, example_input)

# Save the traced model
traced_script_module.save(pt_file_path)

print(f"Model successfully converted and saved to {pt_file_path}")