set -e
cd ..
rm -rf ./src ./include
tar -zxf ./code.tar.gz

IFS=$'\n'
for file in `find . -name '._*'`
do
    echo "rm -f ${file}"
    rm -f $file
done

make dep
make
rm -rf /var/www/cgi-bin/*
cp -f cgi-bin/* /var/www/cgi-bin/
cp -f src/Python/*.py /var/www/cgi-bin/
chmod 755 /var/www/cgi-bin/*
chmod 755 /cgi-bin/*
