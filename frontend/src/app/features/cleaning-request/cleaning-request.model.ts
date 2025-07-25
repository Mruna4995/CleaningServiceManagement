export interface CleaningRequest {
  id: number;
  name: string;
  email: string;
  phone: string;
  roomNo: string;
  address: string;
  locationUrl?: string;
  serviceType: string;
  status?: string;
  createdAt?: string;
  assignedTo?: string;
  staffEmail?: string;
  selectedStaff?: { name: string; email: string };
}
