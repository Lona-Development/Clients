from setuptools import setup

from pathlib import Path
this_directory = Path(__file__).parent
long_description = (this_directory / "README.md").read_text()

setup(
    name="lonadb-client",
    version="2.3",
    author="Lona-Development",
    author_email="collin@lona-development.org",
    description="A client library for interacting with LonaDB server",
    url="https://git.lona-development.org/Lona-Development/Clients",
    packages=["lonadb_client"],
    long_description=long_description,
    long_description_content_type='text/markdown',
    license="MIT",
    classifiers=[
        "Development Status :: 5 - Production/Stable",
        "Intended Audience :: Developers",
        "License :: OSI Approved :: MIT License",
        "Programming Language :: Python :: 3",
        "Programming Language :: Python :: 3.6",
        "Programming Language :: Python :: 3.7",
        "Programming Language :: Python :: 3.8",
        "Programming Language :: Python :: 3.9",
    ],
    keywords="lonadb client database",
    platforms="any",
    install_requires=[
        "pycryptodome>=3.10.1"
    ]
)
