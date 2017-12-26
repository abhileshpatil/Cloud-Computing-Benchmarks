#include <memory.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

void evaluation(int blockSize);
void OperationAccess();
void SequentialWrite();
void RandomWrite();
void ReadWrite();

double OperationsCount = 450*1024*1024;
int noOfThreads;
clock_t startTime_seqwrite, endTime_seqwrite, startTime_randwrite, endTime_randwrite, startTime_readwrite, endTime_readwrite;
void *mem_char;
FILE *fPointer;
int blockSize;

double totaltime_seqwrite = 0.0, time_diff_seqwrite = 0.0;
double time_diff_randwrite = 0.0, totaltime_randwrite = 0.0;
double time_diff_readwrite = 0.0, totaltime_readwrite = 0.0;

int main(int argc, int *argv[]){
    printf("Enter the number of threads for the memory operations\n");
    scanf("\n %d", &noOfThreads);

    pthread_t threads[noOfThreads];
    fPointer = fopen( "memory_evaluation.txt", "ab" );
    
    int blockSizeArr[] = {8, 8*1024, 8*1024*1024, 80*1024*1024};

    for(int i = 0; i < sizeof(blockSizeArr) / sizeof( *blockSizeArr); i++){
        blockSize = blockSizeArr[i];
        // mem_char = (int*)malloc(sizeof(int) * OperationsCount);

        for (int j = 0; j < noOfThreads ; j++) {
            pthread_create(&threads[j], NULL, (void *)OperationAccess, NULL);
        }

        for (int k = 0; k < noOfThreads; k++) {
            pthread_join(threads[k], NULL);
        }
        evaluation(blockSize);
    }
    return 0;
}

void OperationAccess(){
    // int* mem_char_seqwrite = (int*)mem_char;
    // int* mem_char_randwrite = (int*)mem_char;
    // int* mem_char_readwrite = (int*)mem_char;
    
    SequentialWrite();
    totaltime_seqwrite = totaltime_seqwrite + time_diff_seqwrite;
//    printf("\ntotal time for seq write: %f\n", totaltime_seqwrite);

    RandomWrite();
    totaltime_randwrite = totaltime_randwrite + time_diff_randwrite;
//    printf("\ntotal time for rand write: %f\n", totaltime_randwrite);

    ReadWrite();
    totaltime_readwrite = totaltime_readwrite + time_diff_readwrite;
//    printf("\ntotal time for read+write: %f\n", totaltime_readwrite);
}

void SequentialWrite(){
//    printf("\ninside seq write method\n");
    int *seq_buf = (int*)malloc(sizeof(int)*OperationsCount);
    int iterations = (int)(long)((OperationsCount)/blockSize);
//    printf("iterartion chu:- %f",iterations);
    int i;
    startTime_seqwrite = clock();
    for (i = 0; i < iterations; i++) {
        // printf("value of i %d\n", i);
		memset(&seq_buf[i], 1, blockSize);
    }
    endTime_seqwrite = clock();

    time_diff_seqwrite = (double)(endTime_seqwrite - startTime_seqwrite);
    free(seq_buf);
//    printf("time diff is: %f", time_diff_seqwrite);
}

void RandomWrite(){
//    printf("\ninside rand write method\n");
    int iterations = (int)(long)(OperationsCount)/blockSize;
    int *rand_buf = (int*)malloc(sizeof(int)*OperationsCount);
    int i;
    int randSpace;
    startTime_randwrite = clock();
    for (i = 0; i < iterations; i++) {
        randSpace = rand() % (int)(OperationsCount);
		memset(&rand_buf[randSpace], 1, blockSize);
    }
    endTime_randwrite = clock();

    time_diff_randwrite = (double)(endTime_randwrite - startTime_randwrite);
    free(rand_buf);
//    printf("time diff is: %f", time_diff_randwrite );
}

void ReadWrite(){
//    printf("\ninside read write method\n");
    int *write_int = (int*)malloc(sizeof(int) * OperationsCount);
    int *read_int = (int*)malloc(sizeof(int) * OperationsCount);
    int *readwrite_int = (int*)malloc(sizeof(int) * OperationsCount);
    int iterations = (int)(long)(OperationsCount)/blockSize;
    int i;
    startTime_readwrite = clock();
    for (i = 0; i < iterations; i++) {
		memcpy(write_int, read_int+i*blockSize, blockSize);
		memset(readwrite_int+i*blockSize, *write_int, blockSize);
    }
    endTime_readwrite = clock();

    time_diff_readwrite = (double)(endTime_readwrite - startTime_readwrite);
//    printf("time diff is: %f", time_diff_readwrite );
    free(write_int);
    free(read_int);
    free(readwrite_int);
}

void evaluation(int blockSize){
//    printf("evaluating throughput & latency...\n");
    fprintf(fPointer, "Operations: %f Threads: %d Blocksize: %d\n", OperationsCount, noOfThreads, blockSize);
    
    fprintf(fPointer, "Sequential Write:\n");
    fprintf(fPointer, "Throughput: %f \n", ((noOfThreads*OperationsCount/(1024*1024))/(totaltime_seqwrite/ CLOCKS_PER_SEC)));
    fprintf(fPointer, "Latency: %f \n", ((totaltime_seqwrite/ CLOCKS_PER_SEC)*1000)/((noOfThreads*OperationsCount)/(1024*1024)));
    // printf("\nSequential Write:\n");
    // printf("Operations: %f threads %d: Time %f throughput: %f \n", OperationsCount, noOfThreads, totaltime_seqwrite, ((noOfThreads*OperationsCount/(1024*1024))/(totaltime_seqwrite/ CLOCKS_PER_SEC)));
    // printf("Latency for sequential write: %f \n", ((totaltime_seqwrite/ CLOCKS_PER_SEC)*1000)/((noOfThreads*OperationsCount)/(1024*1024)));

    fprintf(fPointer, "Random Write:\n");
    fprintf(fPointer, "Throughput: %f \n", ((noOfThreads*OperationsCount/(1024*1024))/(totaltime_randwrite/ CLOCKS_PER_SEC)));
    fprintf(fPointer, "Latency: %f \n", ((totaltime_randwrite/ CLOCKS_PER_SEC)*1000)/((noOfThreads*OperationsCount)/(1024*1024)));
    // printf("Random Write:\n");
    // printf("Operations: %f threads %d: Time %f throughput: %f \n", OperationsCount, noOfThreads, totaltime_randwrite, ((noOfThreads*OperationsCount/(1024*1024))/(totaltime_randwrite/ CLOCKS_PER_SEC)));
    // printf("Latency for sequential write: %f \n", ((totaltime_randwrite/ CLOCKS_PER_SEC)*1000)/((noOfThreads*OperationsCount)/(1024*1024)));

    fprintf(fPointer, "Read + Write:\n");
    fprintf(fPointer, "Throughput: %f \n", ((noOfThreads*OperationsCount/(1024*1024))/(totaltime_readwrite/ CLOCKS_PER_SEC)));
    fprintf(fPointer, "Latency: %f \n", ((totaltime_readwrite/ CLOCKS_PER_SEC)*1000)/((noOfThreads*OperationsCount)/(1024*1024)));
    // printf("Read + Write:\n");
    // printf("Operations: %f threads %d: Time %f throughput: %f \n", OperationsCount, noOfThreads, totaltime_readwrite, ((noOfThreads*OperationsCount/(1024*1024))/(totaltime_readwrite/ CLOCKS_PER_SEC)));
    // printf("Latency for read + write: %f \n", ((totaltime_readwrite/ CLOCKS_PER_SEC)*1000)/((noOfThreads*OperationsCount)/(1024*1024)));

    totaltime_seqwrite = 0.0, time_diff_seqwrite = 0.0;
    time_diff_randwrite = 0.0, totaltime_randwrite = 0.0;
    time_diff_readwrite = 0.0, totaltime_readwrite = 0.0;
}
