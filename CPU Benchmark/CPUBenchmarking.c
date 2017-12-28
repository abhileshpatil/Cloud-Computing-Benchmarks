#include<pthread.h>
#include<stdio.h>
#include<time.h>
#include <immintrin.h>

void computeFLOPS();
void computeIOPS();
void FLOPS();
void IOPS();

unsigned long time_diff,time_diff1;
float fcal,ical,fcal1,ical1;
clock_t startTime, endTime;

FILE *fPointer;
FILE *fPointer2;
FILE *fPointer3;

float totalOPS;

pthread_t FLOPSThreads[8];
pthread_t IOPSThreads[8];

long flopOperations[8];
long iopOperations[8];


int main(){
    fPointer = fopen( "CPUBenchmarkLog.txt", "ab" );
    fPointer2 = fopen( "FlopSamples.txt", "ab" );
    fPointer3 = fopen( "IopSamples.txt", "ab" );

    int noOfThreads;
    printf("Enter the number of threads \n");
    scanf("%d", &noOfThreads);
    computeFLOPS(noOfThreads);
    computeIOPS(noOfThreads);
    return 0;
}

void computeFLOPS(int noOfThreads){
    int i;
    for(i = 0; i < noOfThreads; i++){
        pthread_create(&FLOPSThreads[i], NULL, (void *)FLOPS, NULL);
    }
    int j;
    for(j = 0; j < noOfThreads; j++){
        pthread_join(FLOPSThreads[j], NULL);
    }    
    printf("%d Thread GFLOPS: %f", noOfThreads, (noOfThreads*(fcal/1000000000)));
    fprintf(fPointer,"\n %d Thread GFLOPS: %f \n", noOfThreads, (noOfThreads*(fcal/1000000000)));
}


void computeIOPS(int noOfThreads){
    int i;
    for(i = 0; i < noOfThreads; i++){
        pthread_create(&IOPSThreads[i], NULL, (void *)IOPS, NULL);
    }
    int j;
    for(j = 0; j < noOfThreads; j++){
        pthread_join(IOPSThreads[j], NULL);
    }

    printf("%d Thread GIOPS: %f", noOfThreads, noOfThreads*(ical/1000000000));
    fprintf(fPointer,"\n %d Thread GIOPS: %f \n", noOfThreads, noOfThreads*(ical/1000000000));
}

void FLOPS(){
    long sum, i;
    startTime = clock();
    __m256 vec1 = _mm256_set_ps(2.52, 4.44, 6.14, 8.90, 10.82, 12.49, 14.41, 16.73);
    __m256 vec2 = _mm256_set_ps(1.7, 3.1, 5.9, 7.7, 9.4, 11.0, 9.3, 6.1);
    for(i=0;i<1000000000;i++){
        _mm256_add_ps(vec1,vec2);
        _mm256_sub_ps(vec1,vec2);
        _mm256_mul_ps(vec1,vec2);
    }
    endTime=clock();
    time_diff = (long)(endTime - startTime) / CLOCKS_PER_SEC;
    fcal=(24*1000000000ul)/((time_diff));

}

void IOPS(){
    long i=0;
    int sum;
    startTime = clock();
    __m256i vec3 = _mm256_set_epi32(1, 2, 3, 4, 5, 6, 7, 8);
    __m256i vec4 = _mm256_set_epi32(2, 4, 6, 8, 10, 12, 14, 16);
	for(i=0;i<1000000000;i++){
        _mm256_add_epi32(vec3,vec4);
        _mm256_sub_epi32(vec4,vec3);
	}
    endTime=clock();
    time_diff1 = (long)((endTime - startTime))/ CLOCKS_PER_SEC;
	ical=(16*1000000000ul)/((time_diff1));
}

