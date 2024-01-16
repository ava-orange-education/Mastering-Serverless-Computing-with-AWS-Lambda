const AWS = require('aws-sdk');

const kms = new AWS.KMS();

exports.handler = async (event) => {
    try {
        // Example: Encrypt a secret and store it in AWS Systems Manager Parameter Store
        const plaintextSecret = process.env.ENV_VAR;
        const encryptParams = {
            KeyId: process.env.KMS_Key_Id,
            Plaintext: Buffer.from(plaintextSecret)
        };
        const encryptedData = await kms.encrypt(encryptParams).promise();
        
        console.log("Encrypted Secret: ",encryptedData)
        // Example: Decrypt the secret at runtime
        const decryptParams = {
            CiphertextBlob: encryptedData.CiphertextBlob
        };

        const decryptedData = await kms.decrypt(decryptParams).promise();
        const decryptedSecret = decryptedData.Plaintext.toString();

        console.log('Decrypted Secret: ', decryptedSecret);

        const response = {
            statusCode: 200,
            body: JSON.stringify('Environment variable encryption and decryption successful!'),
        };

        return response;
    } catch (error) {
        console.error('Error:', error);

        const response = {
            statusCode: 500,
            body: JSON.stringify('Error occurred during encryption and decryption.'),
        };

        return response;
    }
};
