package paging;

import java.awt.*;
import java.io.FileNotFoundException;
import java.util.*;
import java.io.File;

/**
 * Created by sarahaly on 11/25/16.
 */
public class Paging {
    public static Page [] frameTable;
    public static void main(String [] args) throws FileNotFoundException{
        String line = "20 10 10 2 10 lifo 0";
        String [] parts = line.split(" ");
        int cnt = 0;
        int quantum = 3;
        Scanner randInput = new Scanner(new File("random-numbers.txt"));

        //get machine info -- create machine info object
        MachineInfo machineInfo = new MachineInfo(Integer.parseInt(parts[cnt++]),
                Integer.parseInt(parts[cnt++]),
                Integer.parseInt(parts[cnt++]),
                Integer.parseInt(parts[cnt++]),
                Integer.parseInt(parts[cnt++]),
                parts[cnt++] );

        //print out the machine info
        System.out.println(machineInfo.toString());

        //create frame table && fill it with empty pages
        frameTable = new Page[machineInfo.machineSize/machineInfo.pageSize];
        Arrays.fill(frameTable, new Page(-1, -1));

        //create page queue
        Queue<Page> pageQueue = new LinkedList<Page>();
        Stack<Page> pageStack = new Stack<Page>();

        //check if j = 1
        if (machineInfo.jobMix == 1 ){
            //create a single process
            int numProcesses = 1;
            int A = 1, B = 0, C = 0;
            HashMap<Integer, Process> processHashMap = getProcesses(numProcesses, A, B, C, machineInfo);

            //for each process
            for(int j = 1; j <= numProcesses ; j++) {
                Process process = processHashMap.get(j);
                //for num of references
                for (int i = 1; i <= machineInfo.numReferences; i++) {
                    //simulate paging
                    pagingSimulation(randInput, machineInfo, pageQueue, pageStack, process, i, processHashMap);
                }

                //print stats
                System.out.println(" ");

            }
            printResults(processHashMap, machineInfo);
        }
        //if j = 2
        else if (machineInfo.jobMix == 2 ){
            //create the 4 processes && add them to a hastable
            int numProcesses = 4;
            int A = 1, B = 0, C = 0;
            HashMap<Integer, Process> processHashMap = getProcesses(numProcesses, A, B, C, machineInfo);

            int time = 0;
            /*while(processHashMap.get(1).refsLeft > 0 &&
                    processHashMap.get(2).refsLeft > 0 &&
                    processHashMap.get(3).refsLeft > 0 &&
                    processHashMap.get(4).refsLeft > 0){*/
                //for each process
                for(int j = 0; j <= numProcesses * machineInfo.numReferences; j++)

                    for(int i = 1; i <= numProcesses; i++){
                        Process process = processHashMap.get(i);

                        //simulate number of references
                        for (int k = 0; k < quantum; k++) {
                            if (process.refsLeft > 0) {
                                time++;
                                pagingSimulation(randInput, machineInfo, pageQueue, pageStack, process, time, processHashMap);
                                process.refsLeft--;
                            } else {
                                break;
                            }
                        }

                }
           // }
            printResults(processHashMap, machineInfo);

        }
        else if (machineInfo.jobMix == 3 ){

            //create the 4 processes && add them to a hastable
            int numProcesses = 4;
            int A = 0, B = 0, C = 0;
            HashMap<Integer, Process> processHashMap = getProcesses(numProcesses, A, B, C, machineInfo);

            int time = 0;
            /*while(processHashMap.get(1).refsLeft > 0 &&
                    processHashMap.get(2).refsLeft > 0 &&
                    processHashMap.get(3).refsLeft > 0 &&
                    processHashMap.get(4).refsLeft > 0){*/
                //for each process
                for(int j = 0; j <= numProcesses * machineInfo.numReferences; j++)

                    for(int i = 1; i <= numProcesses; i++){
                        Process process = processHashMap.get(i);

                        //simulate number of references
                        for (int k = 0; k < quantum; k++) {
                            if (process.refsLeft > 0) {
                                time++;
                                pagingSimulation(randInput, machineInfo, pageQueue, pageStack, process, time, processHashMap);
                                process.refsLeft--;
                            } else {
                                break;
                            }
                        }

                    }
            //}
            printResults(processHashMap, machineInfo);

        }
        else if (machineInfo.jobMix == 4 ){



        }
    }

    public static int contains(Page [] arr, Page page){
        for(int i = 0; i < arr.length; i++) {
            if(arr[i].equals(page)) {
                return i;
            }
        }
        return -1;
    }

    //returns the index that the page was added at
    public static int add(Scanner randInput, String algo, Page page, Page [] arr, Queue<Page> pageQueue, int currTime, Process process, HashMap<Integer, Process> processHashMap, Stack<Page> pageStack){
        //find an empty spot in the arr
        for(int i = arr.length-1; i >= 0; i--) {
            if(arr[i].equals(new Page(-1,-1))) {
                //iterate through queue
                Iterator<Page> iterator = pageQueue.iterator();

                //find it -- remove it
                while(iterator.hasNext()) {
                    Page p = iterator.next();

                    if(page.equals(p)) {
                        iterator.remove();
                    }
                }

                page.loadTime = currTime;
                System.out.println("\tAdded page " + page.pageNum + " at frame " + i);
                arr[i] = page;
                return i;
            }
        }

        //if there isn't an empty one
        //evict one
        int evictedPageIndex = evict(randInput, algo, arr, page, pageQueue, pageStack);

        if(evictedPageIndex >= 0) {
            Page evictedPage = arr[evictedPageIndex];
            System.out.println("\tEvicted page " + evictedPage.pageNum + " of " + evictedPage.processNum);
            page.loadTime = currTime;
            processHashMap.get(evictedPage.processNum).residencyTime += currTime - evictedPage.loadTime;
            processHashMap.get(evictedPage.processNum).numEvictions++;

            arr[evictedPageIndex] = page;
            if(algo.compareToIgnoreCase("lru") == 0){
                pageQueue.add(page);
            }
            else if (algo.compareToIgnoreCase("lifo") == 0) {
                pageStack.add(page);
            }

            return evictedPageIndex;
        }

        //this shouldn't happen
        System.out.println("Add function is returning -1");
        return -1;
    }

    public static int evict(Scanner randInput, String algo, Page [] arr, Page page, Queue<Page> pageQueue, Stack<Page> pageStack){

        //if we're evicting the least recently used
        if(algo.compareToIgnoreCase("lru") == 0) {
            //pop the lru
            Page pageLru = pageQueue.poll();

            //get the index of lru page in the array
            for(int i = 0; i < arr.length; i++) {
                if(arr[i].equals(pageLru)) {
                    return i;
                }
            }
        }
        else if (algo.compareToIgnoreCase("lifo") == 0) {
           //get the page to evict from the stack
            Page pageToEvict = pageStack.pop();

            //get the index of evicted page in the array
            for(int i = 0; i < arr.length; i++) {
                if(arr[i].equals(pageToEvict)) {
                    return i;
                }
            }

        }
        else if(algo.compareToIgnoreCase("random") == 0) {
           // int randIndex = random.nextInt(arr.length - 0 + 1) + 0
            int randNum = randomNum(randInput);
        }

        //this shouldn't happen
        System.out.println("Evict function is returning -1");
        return -1;

    }

    public static int randomNum(Scanner randInput) {
        int val = randInput.nextInt();
        return val;
    }

    public static double getRandomNum(Scanner randInput) {
        double val = randInput.nextInt();
        return val / (Integer.MAX_VALUE + 1.0);
    }

    public static void printResults(HashMap<Integer, Process> processHashMap, MachineInfo machineInfo){

        int totalFaults = 0;
        double totalAvgRes = 0.0;
        int totalEvictions = 0;
        int size = processHashMap.keySet().size();

        System.out.println(" ");
        for(int i = 1; i <= size; i++) {
            Process process = processHashMap.get(i);
            totalEvictions += process.numEvictions;
            //if the process had no evictions
            if (process.numEvictions == 0) {
                totalFaults += process.numFaults;
                System.out.println("Process " + process.num + " had " + process.numFaults +
                        " faults. With no evictions, the average residence is undefined.");
            }

            else {
                double avgRes = (process.residencyTime * 1.0) / process.numEvictions;
                totalFaults += process.numFaults;
                totalAvgRes += process.residencyTime;
                System.out.println("Process " + process.num + " had " + process.numFaults +
                        " faults and " + avgRes + " average residency.");

            }
        }

        System.out.println(" ");
        if(totalEvictions == 0)
            System.out.println("The total number of faults is " + totalFaults +".\n" +
                "\tWith no evictions, the overall average residence is undefined.");

        else
            System.out.println("The total number of faults is " + totalFaults +" and the" +
                " overall average residence is " + totalAvgRes/totalEvictions + ".");

    }

    public static HashMap<Integer, Process> getProcesses(int numProcesses, double A, double B, double C, MachineInfo machineInfo){
        HashMap<Integer, Process> processHashMap = new HashMap<Integer, Process>();
        for(int j = 1; j <= numProcesses; j++ ) {
            Process p = new Process(j, A, B, C, machineInfo.numReferences);
            processHashMap.put(j, p);
        }
        return processHashMap;
    }

    public static void pagingSimulation(Scanner randInput, MachineInfo machineInfo, Queue<Page> pageQueue, Stack<Page> pageStack, Process process, int i, HashMap<Integer, Process> processHashMap) throws FileNotFoundException{
        double randNum = getRandomNum(randInput);

        //get the word
        int wordNum;

        if (randNum < process.A) {
           // System.out.println("\tCase A");
            wordNum = (111 * process.num + process.w) % machineInfo.processSize;
            process.w++;
        }

        else if (randNum < process.A + process.B) {
            //System.out.println("\tCase B");
            wordNum = (111 * process.num - 5) % machineInfo.processSize;
        }

        else if (randNum < process.A + process.B + process.C) {
            //System.out.println("\tCase C");
            wordNum = (111 * process.num + 4) % machineInfo.processSize;
        }

        else {
            //System.out.println("\tCase D");
            wordNum = (int) (1 - process.A - process.B - process.C) / machineInfo.processSize;
        }

        int pageNum = wordNum / machineInfo.pageSize;
        Page page = new Page(pageNum, process.num);

        int hitIndex = contains(frameTable, page);

        //we have a hit
        if(hitIndex >= 0) {
           //maintain the datastructure for lru
            if(machineInfo.algo.compareToIgnoreCase("lru") == 0){
                //if the page queue is empty
                if(pageQueue.isEmpty()) {
                    pageQueue.add(page);
                }
                //if it doesn't have page
                else if(!pageQueue.contains(page)) {
                    pageQueue.add(page);
                }
                //if it contains it
                else {
                    //iterate through queue
                    Iterator<Page> iterator = pageQueue.iterator();

                    //find it -- remove it
                    while(iterator.hasNext()) {
                        Page p = iterator.next();

                        if(page.equals(p)) {
                            iterator.remove();
                        }
                    }

                    //and then add it again to the end of the queue
                    pageQueue.add(page);
                }
            }
            System.out.println(process.num +
                    " references word " + wordNum + " (page " + page.pageNum + ") at time "
                    + i + ": Hit in frame " + hitIndex);

        }
        //otherwise fault
        else {
            //call add to page to the frametable
            int evicted = add(randInput, machineInfo.algo, page, frameTable, pageQueue, i, process, processHashMap, pageStack);
            process.waitTime++;
            process.numFaults++;

            if(machineInfo.algo.compareToIgnoreCase("lifo") == 0) {
                //add it to the stack
                pageStack.add(page);
            }
            System.out.println(process.num +
                    " references word " + wordNum + " (page " + page.pageNum + ") at time "
                    + i + ": Fault in frame " + evicted);
        }
    }
}

