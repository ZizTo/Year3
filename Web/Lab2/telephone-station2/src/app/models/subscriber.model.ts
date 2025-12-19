export interface Subscriber {
  id?: number;
  fullName: string;
  phoneNumber: string;
  blocked: boolean;
  bills?: Bill[];
}

export interface Service {
  id?: number;
  name: string;
  monthlyCost: number;
}

export interface Bill {
  id?: number;
  subscriber?: Subscriber;
  amount: number;
  issueDate: string;
  paid: boolean;
}
