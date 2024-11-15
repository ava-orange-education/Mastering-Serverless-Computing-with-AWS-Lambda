// Write an enrichService class with a Enrich method accepting an article of type Article interface and simulate a network latency and failure 
// by throwing an exception. The method should return the article with the enriched data.

export interface Message {
  id: string;
  message: string;
  timestamp: string;
}

export class ListenerService {
  Send= async (message: Message): Promise<Message> => {
    const limit = 0.2;
    //Simulate network latency
    await new Promise(resolve => setTimeout(resolve, 2000));
    
    //Simulate random network failure
    const random = Math.random();
    console.log(random);
    if (random < limit) {
      return message;
    }

    if (random >= limit) {
      throw new Error("Network Error");
    }
  }
}
