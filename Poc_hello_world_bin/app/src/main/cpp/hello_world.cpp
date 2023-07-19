#include <stdio.h>
#include <pthread.h>

extern "C";

void print_message() {
    while (1) {
        printf("I am C binary, Hello_World ");
       // sleep(1);
    }
}

int main () {
    printf("I am C binary, from main Hello_World ");

    /*pthread_t tid;
    int ret = pthread_create(&tid, NULL, print_message, NULL);
    if (ret != 0) {
        printf("Failed to create thread");
        return 1;
    }

    while (1) {
        //block in here
        sleep(10);
    }*/
    return 0;
}