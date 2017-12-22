#!/bin/bash
set -e
if [[ $1 == '' ]]; then
    echo '--conf or cgi-bin name'
    exit
fi

if [[ $1 == '--conf' ]]; then
    rm -f ./*.o
    rm -f ./*.d

    if [[ ! -d dependency ]]; then
        mkdir dependency
    fi
    rm -f dependency/*.d

    if [[ ! -d lib ]]; then
        mkdir lib
    fi
    rm -f lib/*.o

    if [[ ! -d cgi-bin ]]; then
        mkdir cgi-bin
    fi
    
    echo '' > depend.d
    make dep
elif [[ $1 == *.py ]]; then
    make dep
    make
    python ./src/Python/$1 <resources/input/${1/./_}.txt
else
    make dep
    make $1 
    ./cgi-bin/$1 <resources/input/$1.txt
fi
