#!/bin/bash

if [ $# -eq 0 ]
then
   BRANCH=master
else
   BRANCH=$1
fi

export JAVA_HOME=/usr

RUNDECK_PLUGIN_NAME=bamboo-rundeck-plugin

RUNDECK_PLUGIN_SCM_URL=git://github.com/connaryscott/${RUNDECK_PLUGIN_NAME}.git
ATLAS_SDK=atlassian-plugin-sdk
ATLAS_SDK_VERSION=3.7.3
ATLAS_SDK_TGZ=${ATLAS_SDK}-${ATLAS_SDK_VERSION}.tar.gz
ATLAS_SDK_URL=https://maven.atlassian.com/content/repositories/atlassian-public/com/atlassian/amps/${ATLAS_SDK}/${ATLAS_SDK_VERSION}/${ATLAS_SDK_TGZ}

ATLAS_SDK_ROOT=$HOME/${ATLAS_SDK}
ATLAS_SDK_HOME=${ATLAS_SDK_ROOT}/${ATLAS_SDK}-${ATLAS_SDK_VERSION}
ATLAS_MVN=${ATLAS_SDK_HOME}/bin/atlas-mvn
if [ ! -f ${ATLAS_MVN} ]
then
   rm -rf ${ATLAS_SDK_HOME}
   if ! mkdir -p ${ATLAS_SDK_HOME}
   then
      echo "unable to create directory: ${ATLAS_SDK_HOME}" 1>&2
      exit 1
   fi
   pushd ${ATLAS_SDK_ROOT} || { echo "unable to pushd into ${ATLAS_SDK_ROOT}" 1>&2; exit 1; }
      if ! curl -o ${ATLAS_SDK_TGZ} -skLf ${ATLAS_SDK_URL}
      then
         echo "unable to download ${ATLAS_SDK_URL} to $(pwd)/${ATLAS_SDK_TGZ}" 1>&2
         exit 1
      fi

      if ! (gunzip -c ${ATLAS_SDK_TGZ} | tar xvf -)
      then
         echo "unable to extract ${ATLAS_SDK_TGZ} into $(pwd)" 1>&2
         exit 1
      fi
   popd
   if [ ! -f ${ATLAS_MVN} ]
   then
      echo "atlas mvn command: ${ATLAS_MVN} does not appear to exist, perhaps ${ATLAS_SDK_TGZ} was not installed" 1>&2
      exit 1
   fi
fi

export PATH=${ATLAS_SDK_ROOT}/bin:${PATH}

if [ ! -d ${RUNDECK_PLUGIN_NAME} ]
then
   if ! git clone ${RUNDECK_PLUGIN_SCM_URL}
   then
      echo "unable to clone ${RUNDECK_PLUGIN_SCM_URL}" 1>&2
      exit 1
   fi
fi
pushd ${RUNDECK_PLUGIN_NAME}
   if ! git checkout ${BRANCH}
   then
      echo "unable to update ${BRANCH} via git checkout" 1>&2
      exit 1
   fi
popd

if [ ! -d ${RUNDECK_PLUGIN_NAME} ]
then
   echo "directory ${RUNDECK_PLUGIN_NAME} does not appear to exist" 1>&2
   exit 1
fi

pushd ${RUNDECK_PLUGIN_NAME}  || { echo "unable to pushd into $(pwd)/${RUNDECK_PLUGIN_NAME}" 1>&2; exit 1; }
   ${ATLAS_MVN} -Dmaven.test.skip=false clean install
popd
