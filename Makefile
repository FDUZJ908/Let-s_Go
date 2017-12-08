SHELL=/bin/bash
SOURCES=./src
INCLUDE=./include
LIBRARY=./lib
BIN=./cgi-bin

CXX=g++
CXXFLAGS=-std=c++11 -I$(INCLUDE)
LINK_LIB=-lPocoNetSSL -lPocoNet -lPocoFoundation -lcrypto -lssl -lmysqlcppconn.7.1.1.9

DEPEND_DIR=./dependency
DEPEND=depend.d
CPPS=$(notdir $(wildcard $(INCLUDE)/*.cpp)) $(notdir $(wildcard $(SOURCES)/*.cpp))
DEPS=$(patsubst %.cpp, %.d, $(CPPS))
ALL=$(patsubst %.cpp, %, $(notdir $(wildcard $(SOURCES)/*.cpp)))
LD=$(patsubst $(INCLUDE)/%.cpp, $(LIBRARY)/%.o, $(wildcard $(INCLUDE)/*.cpp))
RELOBJ=$(notdir $(LD))

VPATH=$(SOURCES):$(INCLUDE):$(LIBRARY):$(BIN):$(DEPEND_DIR)

compile: $(ALL)

%: %.o $(RELOBJ)
	$(CXX) $(LIBRARY)/$(*).o $(LD) -o $(BIN)/$@ $(CXXFLAGS) $(LINK_LIB)

dep: $(DEPEND)
	@echo $(DEPEND) generated

$(DEPEND): $(DEPS)
	@cd $(DEPEND_DIR); cat $(DEPS) > ../$(DEPEND)
	@echo dependency files merged

%.d: %.cpp
	$(CXX) -MM $< -I$(INCLUDE) -o $(DEPEND_DIR)/$@
	@echo -e '\t$(CXX) $< -c -o $(LIBRARY)/$(*).o $(CXXFLAGS)' >> $(DEPEND_DIR)/$@

include $(DEPEND)

install:
	tar --exclude '.*' -zcf code.tar.gz src include Makefile run.sh
	scp code.tar.gz root@shiftlin.top:~/LetsGo
	ssh root@shiftlin.top "bash ~/LetsGo/install.sh"

clean:
	rm -f $(DEPEND_DIR)/*.d
	rm -f $(LIBRARY)/*.o
	rm -f ./*.d
	rm -f ./*.o
	@echo '' > $(DEPEND)

