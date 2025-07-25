import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../../services/dashboard.service'; // create if needed

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  requests: any[] = [];
  cleanerCount: number = 0;

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.loadDashboard();

    // Poll every 10 seconds for real-time effect
    setInterval(() => {
      this.loadDashboard();
    }, 10000);
  }
  pauseVideo(video: HTMLVideoElement) {
  video.pause();
}




  loadDashboard() {
    this.dashboardService.getAllRequests().subscribe((data: any[]) => {
      this.requests = data;
      this.cleanerCount = data.filter(r => r.cleanerAssigned).length;
    });
  }
}
