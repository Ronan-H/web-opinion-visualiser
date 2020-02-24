rm -rf ./docs
cd src
javadoc -d docs/ $(find . -name *.java)
mv docs ..
