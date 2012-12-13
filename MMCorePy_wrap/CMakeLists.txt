
find_package( SWIG REQUIRED )
include( ${SWIG_USE_FILE} )

find_package(PythonLibs)
include_directories(${PYTHON_INCLUDE_PATH})

if( NOT PYTHON_INCLUDE_PATH )
  message( FATAL_ERROR "Unable to locate the Python headers for module development - please install Python and its development files.  On Linux, you may need to install the python-dev or python-devel package.")
endif()

set( Python_ADDTITIONAL_VERSIONS ${PYTHONLIBS_VERSION_STRING} )
find_package( PythonInterp )

if( NOT PYTHON_NUMPY_PATH )
  execute_process( COMMAND ${PYTHON_EXECUTABLE} ${CMAKE_CURRENT_SOURCE_DIR}/findpackage.py numpy OUTPUT_VARIABLE PYTHON_NUMPY_RESULT )
  string( STRIP ${PYTHON_NUMPY_RESULT} PYTHON_NUMPY_RESULT )
  if( PYTHON_NUMPY_RESULT )
  	set( PYTHON_NUMPY_PATH ${PYTHON_NUMPY_RESULT} CACHE PATH "Path to numpy package" FORCE )
  endif()
endif()

if( PYTHON_NUMPY_PATH )
	if( EXISTS ${PYTHON_NUMPY_PATH}/core/include/numpy/arrayobject.h )
		set( PYTHON_NUMPY_INCLUDE ${PYTHON_NUMPY_PATH}/core/include/ CACHE PATH "Path to numpy include directories" )
	endif()
endif()

if( NOT PYTHON_NUMPY_INCLUDE )
  message( FATAL_ERROR "Unable to find headers for the Python numpy module, please install numpy or set PYTHON_NUMPY_INCLUDE to a path containing numpy/arrayobject.h")
endif()

include_directories( ${PYTHON_NUMPY_INCLUDE} )
# 
set_source_files_properties( MMCorePy.i PROPERTIES CPLUSPLUS ON )
set( SWIG_MODULE_MMCorePy_EXTRA_DEPS MMCore )
swig_add_module( MMCorePy python MMCorePy.i )
swig_link_libraries( MMCorePy MMCore MMDevice )
