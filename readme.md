## furlmod

### how I build this shit?

#### Build dependencies:

- BoP 0.5.5
- CodeChickenCore 0.8.7.3
- NEI 1.5.2.28
- EmasherCore 1.6.1
- GasCraft 1.6.2

To compile against BoP's API, you *need* `jsr305-1.3.9` so scalac doesn't shit
itself, because BoP uses guava stuff that scalac doesn't like.

To run, GasCraft and BoP are hard dependencies.

Everything else shouldn't ClassDefEx.  Don't sue me if they do.
