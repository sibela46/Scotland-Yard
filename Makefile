APP = ScotlandYard
IMAGE = scotland_yard
CONTAINER = scotland_yard
DATADIR=${CURDIR}/build
DOCKDIR=/java/ScotlandYard/build

.PHONY: clean buildimage run build

all: clean buildimage build run

buildimage:
	docker build . -t ${IMAGE}

build:
	docker run --name ${CONTAINER} -ti --rm -v ${DATADIR}:${DOCKDIR} ${IMAGE} ./build.sh

run:
	xhost +local:docker
	docker run --name ${CONTAINER} -ti --rm -e DISPLAY=${DISPLAY} -v /tmp/.X11-unix:/tmp/.X11-unix -v ${DATADIR}:${DOCKDIR} ${IMAGE} ./run.sh

clean:
	docker rm ${CONTAINER}
	docker rmi ${IMAGE}
