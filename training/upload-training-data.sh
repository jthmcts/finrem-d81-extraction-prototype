#!/bin/bash

# Set variable values
subscription_id="YOUR_SUBSCRIPTION_ID"
resource_group="YOUR_RESOURCE_GROUP"
location="YOUR_LOCATION" # e.g. uksouth
expiry_date="2026-01-01T00:00:00Z"

# Create a storage account in your Azure resource group
unique_id=$((1 + RANDOM % 99999))
STORAGE_ACCT_NAME="d81form$unique_id"
echo "Creating storage account $STORAGE_ACCT_NAME..."
az storage account create --name "$STORAGE_ACCT_NAME" --subscription "$subscription_id" --resource-group "$resource_group" --location "$location" --sku Standard_LRS --encryption-services blob --default-action Allow --allow-blob-public-access true --only-show-errors --output none

# Get storage key to create a container in the storage account
echo "Getting storage key..."
key_json=$(az storage account keys list --subscription "$subscription_id" --resource-group "$resource_group" --account-name "$STORAGE_ACCT_NAME" --query "[?keyName=='key1'].{keyName:keyName, permissions:permissions, value:value}")
key_string=$(echo "$key_json" | jq -r '.[0].value')
AZURE_STORAGE_KEY=${key_string}

# Create a container
echo "Creating container..."
az storage container create --account-name "$STORAGE_ACCT_NAME" --name sampleforms --public-access blob --auth-mode key --account-key "$AZURE_STORAGE_KEY" --output none

# Upload files from your local sample-forms/training-data folder to a container called sampleforms in the storage account
# Each file is uploaded as a blob
echo "Uploading training data..."
az storage blob upload-batch -d sampleforms -s ./sample-forms/training-data --account-name "$STORAGE_ACCT_NAME" --auth-mode key --account-key "$AZURE_STORAGE_KEY" --output none

# Get a Shared Access Signature (a signed URI that points to one or more storage resources) for the blobs in sampleforms
SAS_TOKEN=$(az storage container generate-sas --account-name "$STORAGE_ACCT_NAME" --name sampleforms --expiry "$expiry_date" --permissions rwl --auth-mode key --account-key "$AZURE_STORAGE_KEY")
URI="https://$STORAGE_ACCT_NAME.blob.core.windows.net/sampleforms?$SAS_TOKEN"

# Print the generated Shared Access Signature URI, which is used by Azure Storage to authorize access to the storage resource
echo "-------------------------------------"
echo "SAS URI: $URI"