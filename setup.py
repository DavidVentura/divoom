from distutils.core import setup

setup(
    name='divoom',
    version='0.1',
    packages=['divoom',],
    license='GPLv3',
    long_description=open('README.md').read(),
    entry_points = {
        'console_scripts': ['dvcli=divoom.cli:main',
                            'dvserver=divoom.server:main',
                            'dv-ws-server=web.ws:main'],
    },
    install_requires=[
        'PyBluez==0.22',
        'redis>=3.0.1',
        'pillow>=5.3.0',
        'websocket-server>=0.4',
    ],
    extras_requires={
        'dev': [
            'pytest==4.0.2',
            'pyshark',
        ]
    }
)
