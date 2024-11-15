import { SNSEvent } from "aws-lambda";

export class ItemNotFoundException extends Error {
  private static readonly defaultMessage = "Item not found";
  constructor(message?: string) {
      super(message ?? ItemNotFoundException.defaultMessage);
      this.name = "ItemNotFound";
  }
}
export const handler = async(event: SNSEvent) => {
  throw new ItemNotFoundException();
};