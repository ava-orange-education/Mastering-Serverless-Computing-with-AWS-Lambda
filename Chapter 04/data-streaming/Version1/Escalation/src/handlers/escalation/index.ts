import { KinesisStreamEvent } from "aws-lambda";
import { StartExecutionCommand, SFNClient } from '@aws-sdk/client-sfn';
const client = new SFNClient({ region: process.env.AWS_REGION });

export const handler = async(event: KinesisStreamEvent) => {
  console.log(JSON.stringify(event, null, 2));

  await Promise.all(event.Records.map(async(record) => {
    
    const { category, ticketId, staffId, assignedAt } = JSON.parse(Buffer.from(record.kinesis.data, 'base64').toString());

    const scheduledTime = category == 'HighPriority' ? 10 : 30;
    const params = {
      stateMachineArn: process.env.STATE_MACHINE_ARN,
      input: JSON.stringify({ 
        category,
        ticketId,
        staffId,
        assignedAt,
        scheduledTime 
      })
    };
    const command = new StartExecutionCommand(params);
    await client.send(command);
  }));

  return {
    statusCode: 200,
    body: JSON.stringify({
      message: 'Hello from assignment handler'
    })
  }
};