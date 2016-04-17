#!/bin/bash
export OPENSSL_SOURCE=/home/lvr/Documents/GSRN/Dropbox/Public/GSRN/ITP/git/opcenc/openssl-OpenSSL_1_0_2-stable
mkdir -p objtree/"`uname -s`-`uname -r`-`uname -m`"
cd objtree/"`uname -s`-`uname -r`-`uname -m`"
(cd $OPENSSL_SOURCE; find . -type f) | while read F; do
	mkdir -p `dirname $F`
	rm -f $F; ln -s $OPENSSL_SOURCE/$F $F
	echo $F '->' $OPENSSL_SOURCE/$F
done
make -f Makefile.org clean
