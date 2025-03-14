import java.util.*;

public class CPUScheduling {

    static class Process {
        String pid;
        int arrivalTime;
        int burstTime;
        int waitingTime;
        int turnaroundTime;
        int remainingTime;

        Process(String pid, int arrivalTime, int burstTime) {
            this.pid = pid;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.remainingTime = burstTime;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Process> processes = new ArrayList<>();
        
        System.out.print("Enter number of processes: ");
        int numProcesses = scanner.nextInt();

        // Input process details
        for (int i = 0; i < numProcesses; i++) {
            System.out.print("Enter PID, Arrival Time, and Burst Time for Process " + (i + 1) + ": ");
            String pid = scanner.next();
            int arrivalTime = scanner.nextInt();
            int burstTime = scanner.nextInt();
            processes.add(new Process(pid, arrivalTime, burstTime));
        }

        // Menu for selecting scheduling algorithm
        while (true) {
            System.out.println("\nSelect CPU Scheduling Algorithm:");
            System.out.println("1. FCFS (First-Come, First-Served)");
            System.out.println("2. SJF (Shortest-Job-First)");
            System.out.println("3. SRT (Shortest-Remaining-Time)");
            System.out.println("4. RR (Round Robin)");
            System.out.println("5. Exit");
            System.out.print("Your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    fcfs(processes);
                    break;
                case 2:
                    sjf(processes);
                    break;
                case 3:
                    srt(processes);
                    break;
                case 4:
                    System.out.print("Enter time quantum for RR: ");
                    int timeQuantum = scanner.nextInt();
                    rr(processes, timeQuantum);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // First-Come, First-Served (FCFS) Algorithm
    static void fcfs(List<Process> processes) {
        int totalWaitingTime = 0, totalTurnaroundTime = 0;
        int time = 0;

        System.out.println("\nFCFS Scheduling Results:");
        System.out.println("| PID   | Arrival Time | Burst Time | Waiting Time | Turnaround Time |");
        System.out.println("|--------|--------------|------------|--------------|-----------------|");
        for (Process process : processes) {
            process.waitingTime = time - process.arrivalTime;
            process.turnaroundTime = process.waitingTime + process.burstTime;
            totalWaitingTime += process.waitingTime;
            totalTurnaroundTime += process.turnaroundTime;
            time += process.burstTime;

            System.out.println(String.format("| %-6s | %-12d | %-10d | %-12d | %-15d |", 
                process.pid, process.arrivalTime, process.burstTime, process.waitingTime, process.turnaroundTime));
        }

        printAverages(totalWaitingTime, totalTurnaroundTime, processes.size());
    }

    // Shortest-Job-First (SJF) Algorithm
    static void sjf(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.burstTime)); // Sort by burst time

        int totalWaitingTime = 0, totalTurnaroundTime = 0;
        int time = 0;

        System.out.println("\nSJF Scheduling Results:");
        System.out.println("| PID   | Arrival Time | Burst Time | Waiting Time | Turnaround Time |");
        System.out.println("|--------|--------------|------------|--------------|-----------------|");
        for (Process process : processes) {
            process.waitingTime = time - process.arrivalTime;
            process.turnaroundTime = process.waitingTime + process.burstTime;
            totalWaitingTime += process.waitingTime;
            totalTurnaroundTime += process.turnaroundTime;
            time += process.burstTime;

            System.out.println(String.format("| %-6s | %-12d | %-10d | %-12d | %-15d |", 
                process.pid, process.arrivalTime, process.burstTime, process.waitingTime, process.turnaroundTime));
        }

        printAverages(totalWaitingTime, totalTurnaroundTime, processes.size());
    }

    // Shortest-Remaining-Time (SRT) Algorithm (Preemptive)
    static void srt(List<Process> processes) {
        int totalWaitingTime = 0, totalTurnaroundTime = 0;
        int time = 0;
        PriorityQueue<Process> pq = new PriorityQueue<>(Comparator.comparingInt(p -> p.remainingTime));
        
        int index = 0;
        while (index < processes.size() || !pq.isEmpty()) {
            if (index < processes.size() && processes.get(index).arrivalTime <= time) {
                pq.add(processes.get(index));
                index++;
            }

            if (!pq.isEmpty()) {
                Process currentProcess = pq.poll();
                currentProcess.remainingTime--;
                if (currentProcess.remainingTime == 0) {
                    currentProcess.turnaroundTime = time + 1 - currentProcess.arrivalTime;
                    currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                    totalWaitingTime += currentProcess.waitingTime;
                    totalTurnaroundTime += currentProcess.turnaroundTime;
                }
                time++;
                if (currentProcess.remainingTime > 0) {
                    pq.add(currentProcess);
                }
            } else {
                time++;
            }
        }

        printAverages(totalWaitingTime, totalTurnaroundTime, processes.size());
    }

    // Round Robin (RR) Algorithm
    static void rr(List<Process> processes, int timeQuantum) {
        int totalWaitingTime = 0, totalTurnaroundTime = 0;
        int time = 0;
        Queue<Process> queue = new LinkedList<>(processes);
        
        System.out.println("\nRR Scheduling Results:");
        System.out.println("| PID   | Arrival Time | Burst Time | Waiting Time | Turnaround Time |");
        System.out.println("|--------|--------------|------------|--------------|-----------------|");

        while (!queue.isEmpty()) {
            Process process = queue.poll();
            if (process.burstTime > timeQuantum) {
                time += timeQuantum;
                process.burstTime -= timeQuantum;
                queue.add(process);
            } else {
                time += process.burstTime;
                process.burstTime = 0;
                process.turnaroundTime = time - process.arrivalTime;
                process.waitingTime = process.turnaroundTime - process.remainingTime;
                totalWaitingTime += process.waitingTime;
                totalTurnaroundTime += process.turnaroundTime;
            }

            System.out.println(String.format("| %-6s | %-12d | %-10d | %-12d | %-15d |", 
                process.pid, process.arrivalTime, process.burstTime, process.waitingTime, process.turnaroundTime));
        }

        printAverages(totalWaitingTime, totalTurnaroundTime, processes.size());
    }

    // Print Average Waiting and Turnaround Times
    static void printAverages(int totalWaitingTime, int totalTurnaroundTime, int numProcesses) {
        double averageWaitingTime = (double) totalWaitingTime / numProcesses;
        double averageTurnaroundTime = (double) totalTurnaroundTime / numProcesses;

        System.out.println("\nAverage Waiting Time: " + averageWaitingTime);
        System.out.println("Average Turnaround Time: " + averageTurnaroundTime);
    }
}
