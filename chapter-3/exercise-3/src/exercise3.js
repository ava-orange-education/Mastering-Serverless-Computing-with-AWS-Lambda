// lambda_function/handler.js
const { commonFunction } = require('my_common_module');

exports.handler = async (event) => {
    commonFunction();
    return {
        statusCode: 200,
        body: 'Hello from MyFunction!'
    };
};
