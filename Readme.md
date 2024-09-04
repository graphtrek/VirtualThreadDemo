
### Java Thread
![java_threads.png](java_threads.png)

	•	Egyszerű CPU, 4 mag, Hyper-Threading nélkül: 4 szál.
	•	Modern CPU, 4 mag, Hyper-Threadinggel (2 szál/mag): 8 szál.
	•	Modern CPU, 8 mag, Hyper-Threadinggel (2 szál/mag): 16 szál.

### Blocking Thread
![blocking_thread.png](blocking_thread.png)

### Scaling horizontally or vertically.
![java_threads.png](scaling_options.png)

### Async Blocking Design
![async_block_design.png](async_block_design.png)

### Partial Reactive Design
![partial_reactive_design.png](partial_reactive_design.png)

### Full Reactive Design
![full_reactive_design.png](full_reactive_design.png)

### Virtual Threads
![java_threads.png](virtual_threads.png)

### Virtual Thread
![java_thread.png](virtual_thread.png)


ab -n 20 -c 20 http://localhost:8080/block/2
``` 
server.tomcat.threads.max=1
spring.threads.virtual.enabled=false
``` 

```       
Server Hostname:        localhost
Server Port:            8080

Document Path:          /block/2
Document Length:        39 bytes

Concurrency Level:      20
Time taken for tests:   58.476 seconds
Complete requests:      20
``` 

ab -n 20 -c 20 http://localhost:8080/block/2
``` 
server.tomcat.threads.max=1
spring.threads.virtual.enabled=true
``` 

```       
Server Hostname:        localhost
Server Port:            8080

Document Path:          /block/2
Document Length:        70 bytes

Concurrency Level:      20
Time taken for tests:   7.693 seconds
Complete requests:      20
``` 