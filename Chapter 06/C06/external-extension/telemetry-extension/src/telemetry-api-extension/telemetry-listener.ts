import { createServer } from 'http';
import { telemetryLogModel, telemetryLogModelRecord } from './types';

const LISTENER_HOST = 'sandbox';
const LISTENER_PORT = 4243;
const eventsQueue: telemetryLogModelRecord[] = [];

const onLogReceived = (logs: telemetryLogModel[]) => { 
    console.debug('[Extension] Logs Received ', logs?.length);

    logs.forEach((element: telemetryLogModel) => {
        // The log received is a string, containing diff parts that are separated by '\t' 
        // Example Data :  Timestamp \t RequestId \t Type \t Record
        const record = element.record.split('\t')?.[3];
        eventsQueue.push(record ?? '');
    });
};

const start = (): string => {
    const server = createServer((request, response) => {
        if (request.method == "POST") {
            let body = '';
            request.on("data", (data) => { body += data; });
            request.on("end", () => {
                try { onLogReceived(JSON.parse(body)); } 
                catch (e) { }

                response.writeHead(200, {});
                response.end("OK");
            });
        } else {
            console.error("unexpected request", request.method, request.url);
            response.writeHead(404, {});
            response.end();
        }
    });
      
    server.listen(LISTENER_PORT, LISTENER_HOST);
    return `http://${LISTENER_HOST}:${LISTENER_PORT}`;
}

export default { start, eventsQueue };

