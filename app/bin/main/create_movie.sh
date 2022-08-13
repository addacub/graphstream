#!/bin/bash

#  EXT=png
#  OPT="vcodec=mpeg4:vqscale=2:vhq:v4mv:trell:autoaspect"
#  FPS=1
#  PREFIX="images\treeGraph_"
#  OUTPUT="/home/cubea/Documents/repos/java/graphstream/app/src/main/resources/graphstream-movie.mp4"
 
#  mencoder "mf://$PREFIX*.$EXT" -mf fps=$FPS:type=$EXT -ovc lavc -lavcopts $OPT -o $OUTPUT -nosound -vf scale

PREFIX="/home/cubea/Documents/repos/java/graphstream/app/images/treeGraph_%6d.png"
FPS=1
OUTPUT="/home/cubea/Documents/repos/java/graphstream/app/src/main/resources/graphstreamMovie.mp4"
OUTPUT_FPS=30
CONV_OUTPUT="/home/cubea/Documents/repos/java/graphstream/app/src/main/resources/graphstreamMovie.webm"

ffmpeg -framerate $FPS -i $PREFIX -c:v libx264 -r $OUTPUT_FPS $OUTPUT
ffmpeg -i $OUTPUT $CONV_OUTPUT