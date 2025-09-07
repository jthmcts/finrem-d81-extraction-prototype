# finrem-d81-extraction-prototype
The purpose of this project is to demonstrate how to analyse Financial Remedy D81 forms with a custom Azure AI Document Intelligence model.

The first step is to create an Azure AI Document Intelligence resource in the Azure portal.

This resource can then be used to train a custom model using the D81 forms provided in the `sample-forms/training-data` folder.

The model can then be used to extract data from new D81 forms. An example form is provided in the `sample-forms/validation-data` folder.

## Create an Azure AI Document Intelligence resource
1. In a browser tab, open the Azure portal at https://portal.azure.com, signing in with the Microsoft account associated with your Azure subscription.
2. On the Azure portal home page, navigate to the top search box and type Document Intelligence and then press Enter.
3. On the Document Intelligence page, select Create.
4. On the Create Document Intelligence page, create a new resource with the following settings:
   - Subscription: Your Azure subscription.
   - Resource group: Create or select a resource group
   - Region: Any available region
   - Name: A valid name for your Document Intelligence resource
   - Pricing tier: Free F0 (if you don't have a Free tier available, select Standard S0).
5. When the deployment is complete, select Go to resource to view the resource's Overview page.

## Upload training data to Azure Blob Storage
1. In the `training/upload-training-data.sh` script, modify the subscription_id, resource_group and location variables with the appropriate values for your Azure Document Intelligence resource.
2. Enter the following command to run the script:
   ```bash
   cd ./training
   ./upload-training-data.sh
   ```

## Train the model using Document Intelligence Studio
1. In a browser tab, open the Azure Document Intelligence Studio at https://documentintelligence.ai.azure.com.
2. In the Custom Models section, select the **Custom extraction model** tile.
3. Create a new project, selecting the storage account that was created by the `upload-training-data.sh` script.
4. In the Label data page, select **Train** to train a new model, selecting Build Mode: Template.

## Test the custom model
This can be done either using the Azure Document Intelligence Studio or the Java application provided in this repository.

When running the Java application, ensure that you modify the ENDPOINT, KEY and MODEL_ID variables.
