#!/bin/bash

PREFIX="/home/cubea/Documents/repos/java/graphstream/images/treeGraph_%6d.png"
FPS=1
OUTPUT_MKV="/home/cubea/Documents/repos/java/graphstream/videos/graphstreamMovie.mkv"
CONV_TO_WEBM="/home/cubea/Documents/repos/java/graphstream/videos/graphstreamMovie.webm"
CONV_TO_MP4="/home/cubea/Documents/repos/java/graphstream/videos/graphstreamMovie.mp4"

ffmpeg -framerate $FPS -i $PREFIX  -c:v libx264 -vf format=yuv420p $OUTPUT_MKV
ffmpeg -i $OUTPUT_MKV $CONV_TO_WEBM
ffmpeg -i $OUTPUT_MKV $CONV_TO_MP4


# ffmpeg -framerate $FPS -i $PREFIX -c:v libx264 -r $OUTPUT_FPS $OUTPUT_MP4
# ffmpeg -i $OUTPUT_MP4 $CONV_OUTPUT