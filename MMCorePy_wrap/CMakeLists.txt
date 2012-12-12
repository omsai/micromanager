
find_package( SWIG REQUIRED )
include( ${SWIG_USE_FILE} )

find_package(PythonLibs)
include_directories(${PYTHON_INCLUDE_PATH})

set( Python_ADDTITIONAL_VERSIONS ${PYTHONLIBS_VERSION_STRING} )
find_package( PythonInterp )

if( NOT PYTHON_NUMPY_PATH )
  execute_process( COMMAND ${PYTHON_EXECUTABLE} ${CMAKE_CURRENT_SOURCE_DIR}/findpackage.py numpy OUTPUT_VARIABLE PYTHON_NUMPY_RESULT )
  message( ${PYTHON_NUMPY_RESULT})
  if( PYTHON_NUMPY_RESULT )
  	set( PYTHON_NUMPY_PATH ${PYTHON_NUMPY_RESULT} CACHE PATH "Path to numpy package")
  endif()
endif()

find_path( PYTHON_NUMPY_INCLUDE numpy/arrayobject.h HINTS ${PYTHON_NUMPY_PATH}/core/include/ )

include_directories( ${PYTHON_NUMPY_INCLUDE} )
# 
set_source_files_properties( MMCorePy.i PROPERTIES CPLUSPLUS ON )
set( SWIG_MODULE_MMCorePy_EXTRA_DEPS MMCore )
swig_add_module( MMCorePy python MMCorePy.i )
swig_link_libraries( MMCorePy MMCore MMDevice )
