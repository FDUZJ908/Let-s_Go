set -e
cd ~/LetsGo/
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
cp -f ~/LetsGo/cgi-bin/* /var/www/cgi-bin/
cp -f ~/LetsGo/src/Python/*.py /var/www/cgi-bin/
chmod 755 /var/www/cgi-bin/*
chmod 755 ~/LetsGo/cgi-bin/*
