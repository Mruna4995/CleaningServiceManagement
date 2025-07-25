export interface StaffLoginResponse {
  token: string;
  email: string;
  name: string;
  role: string;
  joinedDate: string;
  assigned: boolean;
}
