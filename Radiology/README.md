# Workshop Prototype Setup: Running on HZDR HPC and Google Colab

This guide provides instructions to run the AI-based radiology prototyping on **HZDR HPC** or **Google Colab**.

# HZDR HPC Access and OPTIMA Jupyter Notebook Setup

This guide provides detailed instructions to help users access the HZDR HPC cluster and launch OPTIMA Jupyter notebooks for highly computational tasks, such as AI/ML workflows requiring powerful GPUs.

---

## Table of Contents
1. [Overview](#overview)
2. [SSH Key Generation](#ssh-key-generation)
3. [SSH Configuration](#ssh-configuration)
   - [Linux](#linux)
   - [macOS](#macos)
   - [Windows](#windows)
4. [Launching OPTIMA Jupyter Notebooks on HPC](#launching-optima-jupyter-notebooks-on-hpc)
   - [Terminal 1: Start Interactive Session](#terminal-1-start-interactive-session)
   - [Terminal 2: Set Up SSH Tunnel](#terminal-2-set-up-ssh-tunnel)

---

## Overview

For highly computational tasks, especially those requiring powerful GPUs, it is recommended to launch the OPTIMA-specialized Jupyter notebooks on the HZDR HPC cluster. Follow the steps below for configuring SSH and starting the notebook. For **Linux/macOS**, open two terminal windows; for **Windows**, use an SSH client such as PuTTY to create a similar setup.

---

## SSH Key Generation

Before configuring SSH, you need to create an SSH key if you don't already have one. Follow the instructions for your operating system below:

### Linux and macOS

1. **Open Terminal**.
2. **Generate SSH Key**:
   - Run the following command:
     ```bash
     ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
     ```
   - Replace `"your_email@example.com"` with your email address.
   - Press `Enter` to accept the default file location (usually `~/.ssh/id_rsa`).
   - Optionally, set a passphrase for added security or press `Enter` to skip.

3. **Add the SSH Key to the SSH Agent**:
   - Start the SSH agent:
     ```bash
     eval "$(ssh-agent -s)"
     ```
   - Add your SSH private key to the agent:
     ```bash
     ssh-add ~/.ssh/id_rsa
     ```

### Windows

1. **Open PowerShell**.
2. **Generate SSH Key**:
   - Run the following command:
     ```powershell
     ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
     ```
   - Replace `"your_email@example.com"` with your email address.
   - Press `Enter` to accept the default file location (usually `C:\Users\<YourUsername>\.ssh\id_rsa`).
   - Optionally, set a passphrase for added security or press `Enter` to skip.

3. **Add the SSH Key to the SSH Agent**:
   - Start the SSH agent:
     ```powershell
     Start-Service ssh-agent
     ```
   - Add your SSH private key to the agent:
     ```powershell
     ssh-add $HOME\.ssh\id_rsa
     ```

---

## SSH Configuration

### Prerequisites
- Ensure you have an SSH key (`id_rsa` or equivalent) in your `.ssh` directory.

The SSH configuration helps set up access to both the UNIX Terminal Server and the Hemera login node. Follow the instructions for your operating system.

### Linux

1. **Locate or Create the SSH Config File**:
   - Open a terminal and create the config file if it doesn’t exist:
     ```bash
     mkdir -p ~/.ssh
     nano ~/.ssh/config
     ```

2. **Add Configuration for UNIX Terminal Server and Hemera Login Node**:
   - Paste the following configuration into the file, replacing `<HZDR username>` with your actual HZDR username:
     ```plaintext
     # UNIX terminal server
     Host uts
         HostName uts.hzdr.de
         User <HZDR username>
         IdentityFile ~/.ssh/id_rsa
         AddKeysToAgent yes
         ForwardAgent yes

     # Hemera login node
     Host hemera5
         HostName hemera5.fz-rossendorf.de
         User <HZDR username>
         ProxyJump uts
         ForwardAgent yes
     ```

### macOS

1. **Locate or Create the SSH Config File**:
   - Open a terminal and create the config file if it doesn’t exist:
     ```bash
     mkdir -p ~/.ssh
     nano ~/.ssh/config
     ```

2. **Add Configuration for UNIX Terminal Server and Hemera Login Node**:
   - Paste the following configuration into the file, replacing `<HZDR username>` with your actual HZDR username:
     ```plaintext
     # UNIX terminal server
     Host uts
         HostName uts.hzdr.de
         User <HZDR username>
         IdentityFile ~/.ssh/id_rsa
         AddKeysToAgent yes
         ForwardAgent yes

     # Hemera login node
     Host hemera5
         HostName hemera5.fz-rossendorf.de
         User <HZDR username>
         ProxyJump uts
         ForwardAgent yes
     ```

### Windows

1. **Locate or Create the SSH Config File Using PowerShell**:
   - Open PowerShell and create the config file:
     ```powershell
     mkdir $HOME\.ssh -ErrorAction SilentlyContinue
     notepad $HOME\.ssh\config
     ```

2. **Add Configuration for UNIX Terminal Server and Hemera Login Node**:
   - Paste the following configuration into the file, replacing `<HZDR username>` with your actual HZDR username:
     ```plaintext
     # UNIX terminal server
     Host uts
         HostName uts.hzdr.de
         User <HZDR username>
         IdentityFile ~/.ssh/id_rsa
         AddKeysToAgent yes
         ForwardAgent yes

     # Hemera login node
     Host hemera5
         HostName hemera5.fz-rossendorf.de
         User <HZDR username>
         ProxyJump uts
         ForwardAgent yes
     ```

---

## Launching OPTIMA Jupyter Notebooks on HPC

For high-performance tasks, follow these steps to set up and access a Jupyter notebook on the HZDR HPC. In the first terminal the Jupyter notebook will be launched as an encapsulated **Apptainer** container within an interactive job on the cluster. Apptainer is similar to Docker, but takes into account the special security architecture of HPC systems, among other things.
**Note**: Open two terminals to proceed with the setup.

### Terminal 1: Start Interactive Session

1. **Log in to Hemera**:
   - Run the following command to log in to Hemera:
     ```bash
     ssh hemera5
     ```

2. **Set the OPTIMA Environment Variable**:
   - Set the OPTIMA environment variable by running:
     ```bash
     export OPTIMA="/bigdata/casus/optima"
     ```

3. **Launch an Interactive Job with GPU**:
   - Request an interactive job with GPU access:
     ```bash
     srun --time 04:00:00 --gres=gpu:1 -p gpu_interactive --pty bash -l -i
     ```

4. **Load the Apptainer Module and Start the Jupyter Notebook Container**:
   - Load Apptainer (a secure container platform for HPC) and launch the notebook:
     ```bash
     module load apptainer/1.3.3
     apptainer run --nv --bind $OPTIMA/prototyping_202411:$HOME/prototyping_202411 $OPTIMA/containerimages/optima-jupyter-notebook_latest.sif
     ```

### Terminal 2: Set Up SSH Tunnel

1. **Open a second terminal** and set up an SSH tunnel to forward the notebook port:
   - Run the following command, replacing `<HZDR username>` with your HZDR username:
     ```bash
     ssh -L 8890:localhost:8890 -J hemera5 <HZDR username>@gv025
     ```

2. **Access the Jupyter Notebook**:
   - Open a web browser and go to [http://localhost:8890/](http://localhost:8890/) to access the Jupyter notebook.

---

# Dataset Access

You can access the datasets via the following links:

### Google Drive
[![Google Drive](https://www.google.com/url?sa=i&url=https%3A%2F%2Flogos-world.net%2Fgoogle-drive-logo%2F&psig=AOvVaw06NO_xAL820lxgOHjZ888g&ust=1730895035832000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJjk4tCUxYkDFQAAAAAdAAAAABAE)](https://drive.google.com/drive/folders/10Pzju8Tx3M4BKY-5r26mcNa1IcwnUZc0?usp=sharing)
[Google Drive Link](https://drive.google.com/drive/folders/10Pzju8Tx3M4BKY-5r26mcNa1IcwnUZc0?usp=sharing)

### OneDrive
[![OneDrive](https://upload.wikimedia.org/wikipedia/commons/thumb/1/12/OneDrive_Logo.png/512px-OneDrive_Logo.png)](https://qmulprod-my.sharepoint.com/:f:/r/personal/hfy432_qmul_ac_uk/Documents/AI4MedTech/OPTIMA/Prototype%20Workshop%20Nov%2024?csf=1&web=1&e=UnbJoa)
[OneDrive Link](https://qmulprod-my.sharepoint.com/:f:/r/personal/hfy432_qmul_ac_uk/Documents/AI4MedTech/OPTIMA/Prototype%20Workshop%20Nov%2024?csf=1&web=1&e=UnbJoa)



## Dataset & Source Codes Directory Structure 

![Directory Flowchart](https://drive.google.com/uc?id=19ekUe4giX42pGK_gslHYrC-toB1Hiv4V)



# Running the Script on Google Colab 
![Google Colab Logo](https://colab.research.google.com/img/colab_favicon.ico)


Google Colab is a free cloud-based platform that allows you to run Python code in your browser without any setup.

## Prerequisites

- A Google account to access Google Colab.

## Steps to Run the Script

1. **Open Google Colab**
   - Go to [Google Colab](https://colab.research.google.com/).
   - Open the `main_I_GoogleColab.ipynb` or `main_II_GoogleColab.ipynb`
   - Click on `Runtime` in the menu.
   - Select `Change runtime type`.
   - In the "Hardware accelerator" dropdown, select `GPU` and choose `T4` if available.
   - Click on the play button next to the code cell to execute the script. Alternatively, you can run all cells by clicking on `Runtime` in the menu and selecting `Run all`.

5. **View the Output**
   - After the script finishes running, you can see the output directly below the code cell.
