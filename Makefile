APP = ScotlandYard
IMAGE = scotland_yard
CONTAINER = scotland_yard

.PHONY: clean install run

all: clean install run

install:
	docker build . -t ${IMAGE}

run:
	xhost +local:docker
	docker run --name ${CONTAINER} -ti --rm -e DISPLAY=${DISPLAY} -v /tmp/.X11-unix:/tmp/.X11-unix ${IMAGE}

clean:
	docker rm ${CONTAINER}
	docker rmi ${IMAGE}
