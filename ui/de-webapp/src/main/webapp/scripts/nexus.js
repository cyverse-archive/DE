CodeMirror
		.defineMode(
				'nexus',
				function() {

					var words = {};
					function define(style, string) {
						var split = string.split(' ');
						for ( var i = 0; i < split.length; i++) {
							words[split[i]] = style;
						}
					}
					;

					// Commands
					define('builtin', 'BEGIN END');

					// Keywords
					define('keyword',
							'TAXA CHARACTERS SETS ASSUMPTIONS CODONS UNALIGNED DISTANCES DATA TREES');

					// Attributes
					define(
							'attribute',
							'TAXLABELS CHARLABELS STATELABELS CHARSTATELABELS CHARSET TAXSET GENETICCODE CODESET TREE CODONPOSSET STATESET CHANGESET TREESET CHARPARTITION TAXPARTITION TREEPARTITION USERTYPE WTSET TYPESET EXSET ANCSTATE DIMENSIONS FORMAT ELIMINATE MATRIX');

					// qualifier
					define(
							'variable-3',
							'DATATYPE STANDARD DNA RNA NUCLEOTIDE PROTEIN CONTINUOUS RESPECTCASE MISSING GAP SYMBOLS EQUATE MATCHCHAR LABELS NOLABELS TRANSPOSE INTERLEAVE ITEMS STATESFORMAT TOKENS NOTOKENS');

					function tokenBase(stream, state) {

						var sol = stream.sol();
						var ch = stream.next();

						if (ch === '#' && sol) {
							stream.skipToEnd();
							return 'def';
						}

						if (ch === '[') {
							stream.eatWhile(/[^\]]/);
							return 'comment';
						}

						stream.eatWhile(/[\w-]/);
						var cur = stream.current().toUpperCase();
						return words.hasOwnProperty(cur) ? words[cur] : null;

					}

					function tokenize(stream, state) {
						return (state.tokens[0] || tokenBase)(stream, state);
					}
					;

					return {
						startState : function() {
							return {
								tokens : []
							};
						},
						token : function(stream, state) {
							if (stream.eatSpace()) {
								return null;
							}
							return tokenize(stream, state);
						}
					};

				});