Inverts an image
The program expects an image with 768 pixels (e.g. 32*24) with the file path given as the first argument
The output will be written to the path given in the second argument e.g. arguments should be:
imageInvert /input/image/path.jpg /output/image/path.jpg

For an image of a different number of pixels, change the 3rd argument of line 7 e.g. STR R0 [R10 #1024] for 1024 pixels
A maximum of 2048 pixels can be used