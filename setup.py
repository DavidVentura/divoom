from distutils.core import setup

setup(
    name='divoom',
    version='0.1',
    packages=['divoom',],
    license='GPLv3',
    long_description=open('README.md').read(),
    entry_points = {
        'console_scripts': ['divoom=divoom.cli:main'],
    },
    install_requires=[
        'pillow>=5.3.0',
    ],
    extras_requires={
        'dev': [
            'pytest==4.0.2',
            'pyshark',
        ]
    }
)
