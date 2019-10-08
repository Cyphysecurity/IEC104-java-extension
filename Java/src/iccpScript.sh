#!/bin/bash  
shopt -s nullglob
# The following are default directory structures which are used by this script: 
#
#	ROOTDIR is the current woring directory or 'pwd' directory which contains all java, python, and this script  
#	OUTPUT contains all the output files that are generated by this script, includes the following sub-dir:
#	   dot is the directory that contains all dot and png 
#          flows holds the output files generated by "pkt2flow" (flows can have sub-dir generated by pkt2flow) 
#          symbols holds  all synbols files generated by Pythoncript
#	INPUT contains all pcap files to be processed
#	 
# To run this script using the above default directory structures, 
# while in the same directory that has this script, type "./dnp3Script.sh"
#
# Optionally, Output, Input, and pkt2flow directories can be specified on the command line when running the script as follow:
#       -To specify a non-default output directory name
#        ./dnp3Script.sh [outputDir Name]
#        Please note that if the outputDir Name directory has not been created 
#        then this script will create this new output directory and all required subdirectories. Else, this script will 
#        simply set OUTPUT to this specified directory name. 
#
#       -OR to specify non-default output AND input directories names
#       ./dnp3Script.sh [outputDir Name] [InputDir Name] 
#        Please note that the InputDir Name must have already been created and contains pcap files, else this
#        script will terminate since there are no pcap files to work on  
#
#       -OR to specify non-default output, input, AND pkt2flow directories names
#	./dnp3Script.sh [outputDir Name] [InputDir Name] [pkt2flowDir Name] 
#        Please note that the pkt2flowDir Name must have already been created and contains pkt2flow app files, else
#        this script will terminate
#
# Be sure to to place pcap files in the input directory prior to running this script, else it will terminate when it finds no pcap files.
# After the script completes, please check the dot directory for DTMC graghs

START_TIME=`date '+%Y-%m-%d_%H:%M:%S'`
echo Start time is: $START_TIME 
 
#Compiling java source files
#echo Compiling Java source files....
#javac *.java

#Current Working Directory of python and java files
ROOTDIR=$(pwd)
echo Working directory is: $ROOTDIR

#Output Directory that stores all output files
if [ $1 ]
then
    if [ ! -d $1 ]
    then
      echo Creating $1 as new outputs directory
      mkdir $1
      echo Creating $1/dot directory
      mkdir $1/dot
      echo Creating $1/flows directory
      mkdir $1/flows
      echo Creating $1/symbols directory
      mkdir $1/symbols
      OUTPUT=$ROOTDIR/$1
    else
      echo directory $1 already existed!
      echo Checking dependent $1 subdirectories, create if needed....
      cd $1
      if [ ! -d "dot" ]
      then
        echo Creating $1/dot directory
        mkdir "dot"
      fi
      if [ ! -d "flows" ]
      then
        echo Creating $1/flows directory
        mkdir "flows"
      fi
      if [ ! -d symbols ]
      then
        echo Creating $1/symbols directory
        mkdir "symbols"
      fi
      OUTPUT=$ROOTDIR/$1
      cd $ROOTDIR
    fi
    echo Output directory is: $OUTPUT
else
    if [ ! -d "outputs" ]
    then
      echo Default outputs directory has not been created, creating outputs directory...
      mkdir "outputs"
      cd "outputs"
        echo Creating outputs/dot directory
        mkdir "dot"
        echo Creating outputs/flows directory
        mkdir "flows"
        echo Creating outputs/symbols directory
        mkdir "symbols"
      OUTPUT=$ROOTDIR/outputs
      cd $ROOTDIR
     else
       OUTPUT=$ROOTDIR/outputs
    fi
    echo Default output directory: $OUTPUT 
fi


#Input Directory that stores all pcaps to be processed 
if [ $2 ]
then
	if [ ! -d $2 ]
	then
           echo $2 is not a valid input directory, terminating script
           exit $?		 
        else
           if [ "$(ls -A $2)" ] 
           then
               INPUT=$ROOTDIR/$2 
           else
               echo "There are no pcap files in input \"$2\" directory, please add pcaps, terminating script!"
               exit $?
           fi 
        fi
        echo Input directory is: $INPUT
else
        INPUT=$ROOTDIR/inputs
        echo Default Input directory is: $INPUT
fi


#Holds pkt2flow output files
FLOWS=$OUTPUT/flows

#Merged pcap output file name
MERGEDPCAP=mergedPcap.pcap

#DNP# filtered pcap file name
ICCP=ICCP$MERGEDPCAP

#Removed any previously merged  and filtered pcap files
for rmfile in $OUTPUT/*.pcapng
do
        echo removing $rmfile
        rm $rmfile
done

#Merge all pcaps files from input directory
#Merged output is limited to 2GB
mergecap -w $OUTPUT/$MERGEDPCAP $INPUT/*.pcapng   

echo Merging pcap files into a single pcap: $MERGEDPCAP

#Filter out only DNP3 packets then save to output directory 
tshark -F libpcap -r $OUTPUT/$MERGEDPCAP -Y "104apci or 104asdu" -w $OUTPUT/$ICCP

echo Filter specific DNP3 packets into: $OUTPUT/$ICCP

# Remove any previously generated flow files
if [ "$(ls -A $FLOWS/)" ]; then 
  rm -r $FLOWS/*
fi


echo Classify packets into flows using pkt2flow application

#Classify packets into flows using the 4-tuple (src_ip, dst_ip, src_port, dst_port)
if [ $3 ]
then
   if [ ! -d $3 ]
   then
     echo Invalid pkt2flow directory $3, terminating this script!
     exit $? 
   else
     PKT2FLOW=$3 
   fi
else
  PKT2FLOW=$ROOTDIR/pkt2flow
  echo Default pkt2flow directory is: $PKT2FLOW
fi

$PKT2FLOW/pkt2flow -uvx -o $FLOWS/ $OUTPUT/$ICCP

for f in $FLOWS/tcp_nosyn/*
do
  OUTFILE=$(echo $f| rev | cut -c 5- | rev | echo "$(cat -)json")
  ##Converts a pcap file to a json file and outputs the json file
  tshark -T json -r $f > $OUTFILE
  echo "Created json file: $OUTFILE"
done

#Remove old symbol files
#if [ "$(ls -A $OUTPUT/symbols)" ]; then 
#   rm $OUTPUT/symbols/*.txt
#fi

echo "Generating symbol files,  please be patient....this may take a while...."

#Generating symbols using Python, tested on Python 2.7
#python2.7 $ROOTDIR/DataExtractorDNP3_SymbolGenerator.py $FLOWS/*/ $OUTPUT/symbols/

# Remove any previously generated dot files
#if [ "$(ls -A $OUTPUT/dot)" ]; then 
#   rm $OUTPUT/dot/*.*
#fi
 
#echo Running: DotFileCreator $OUTPUT/symbols/ $OUTPUT/dot/ 400

#Generating dot file format via java app
#java DotFileCreator $OUTPUT/symbols/ $OUTPUT/dot/ 400

#echo Processing the following dot files: 

#Generating png file for each dot file
#that has been regerated by java app above
#for file in $OUTPUT/dot/*.dot
#do
#	dot -Tpng -O $file 
#        echo $file
#done

#echo The following are DTMC state diagrams have now been created:
#for pngfile in $OUTPUT/dot/*.png
#do
#        echo $pngfile
#done


END_TIME=`date '+%Y-%m-%d_%H:%M:%S'`
echo Finished at: $END_TIME
